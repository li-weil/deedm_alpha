#!/usr/bin/env bash
# convert_to_utf8.sh
# 用法:
#   # dry-run 检测哪些文件不是 UTF-8
#   ./convert_to_utf8.sh --dry-run
#   # 真正转换（会为每个改动文件保留 file.bak）
#   ./convert_to_utf8.sh
#   # 指定项目根目录
#   ./convert_to_utf8.sh /path/to/project
#
set -uo pipefail

# ---------- 用户可调整的配置 ----------
ROOT="${1:-.}"                      # 默认当前目录，也可以传第一个参数指定项目根
DRY_RUN=false
BACKUP_EXT=".bak"
LOGFILE="convert_utf8.log"
# 要处理的文件扩展名（以空格分隔），根据需要扩展
EXTS="java js ts jsx tsx py html css md json xml yml yaml txt properties sh sql"
# 要排除的目录（glob，相对或绝对都可），根据项目补充
EXCLUDES=("**/node_modules/**" "**/.git/**" "**/dist/**" "**/build/**" "**/target/**" "**/venv/**" "**/.venv/**" "**/out/**")
# ---------- 结束配置 ----------

# 解析参数
while (( "$#" )); do
  case "$1" in
    --dry-run) DRY_RUN=true; shift ;;
    --no-backup) BACKUP_EXT=""; shift ;;
    --log) LOGFILE="$2"; shift 2 ;;
    *) shift ;;
  esac
done

# 依赖检查
cmds=(file iconv)
for c in "${cmds[@]}"; do
  if ! command -v "$c" &>/dev/null; then
    echo "缺少依赖: $c，请先安装（例如：sudo apt install -y file iconv uchardet）"
    exit 1
  fi
done
# 可选依赖 uchardet（用于自动检测编码）
UCHARGE_DET_AVAILABLE=true
if ! command -v uchardet &>/dev/null; then
  UCHARGE_DET_AVAILABLE=false
  echo "警告：未安装 uchardet，若遇不能用 gbk 转换的文件，脚本可能无法自动猜测其编码。"
fi

echo "开始（ROOT=${ROOT}）  dry-run=${DRY_RUN}  logfile=${LOGFILE}"
echo "开始时间: $(date)" > "${LOGFILE}"

# 生成 find 的 -iname 参数
find_prune=()
for ex in "${EXCLUDES[@]}"; do
  # transform **/dir/** to -path "./dir/*" style for find -prune
  # we will simply convert to -path patterns: remove leading **/ and trailing /**
  p="$ex"
  p="${p#**/}"
  p="${p%/**}"
  # avoid empty
  [ -n "$p" ] && find_prune+=("-path" "./$p" "-prune" "-o")
done

# build name pattern for find: join extensions
name_args=()
for e in $EXTS; do
  name_args+=("-iname" "*.${e}" "-o")
done
# remove trailing -o
unset 'name_args[${#name_args[@]}-1]'

# find files
mapfile -t files < <(eval "find \"${ROOT}\" ${find_prune[@]:-} -type f \( ${name_args[@]} \) -print0 | xargs -0 -n1 echo")

if [ "${#files[@]}" -eq 0 ]; then
  echo "未找到匹配扩展名的文件。检查 EXTS 或 ROOT。"
  exit 0
fi

printf "检测到 %d 个目标文件\n" "${#files[@]}" | tee -a "${LOGFILE}"

# 逐文件检查并转换
count_need=0
count_converted=0
for f in "${files[@]}"; do
  # skip if file is binary-ish
  mime=$(file -b --mime "$f")
  if echo "$mime" | grep -q "charset=binary"; then
    echo "跳过二进制文件: $f" | tee -a "${LOGFILE}"
    continue
  fi

  # 检测编码（file -i 输出如: text/plain; charset=iso-8859-1）
  enc_line=$(file -i "$f" || true)
  # 提取 charset= 后面的值
  enc=$(echo "$enc_line" | sed -n 's/.*charset=\(.*\)$/\1/p' || true)
  enc=$(echo "$enc" | tr '[:upper:]' '[:lower:]' || true)

  # 标记是否为 utf-8（包含 utf-8 或 us-ascii）
  if echo "$enc" | grep -Eq "utf-8|us-ascii"; then
    # 已经是 utf-8
    continue
  fi

  # 这一路走到的，file 说不是 utf-8；再确认一下内容是否能被 utf-8 正确解码（避免误报）
  if iconv -f utf-8 -t utf-8 "$f" >/dev/null 2>&1; then
    # 可以用 utf-8 解码，视为 utf-8（保险）
    continue
  fi

  # 需要转换
  count_need=$((count_need+1))
  echo "[NEED] $f  (detected: ${enc:-unknown})" | tee -a "${LOGFILE}"

  if [ "$DRY_RUN" = true ]; then
    continue
  fi

  # 备份原文件
  if [ -n "$BACKUP_EXT" ]; then
    cp --preserve=all "$f" "${f}${BACKUP_EXT}"
  fi

  converted=false
  # 优先尝试用 gbk (cp936) 转换（很多 Windows 中文机器是 GBK）
  if iconv -f cp936 -t utf-8 "$f" -o "${f}.tmp" 2>/dev/null; then
    mv "${f}.tmp" "$f" && converted=true
    echo "Converted (cp936 -> utf8): $f" | tee -a "${LOGFILE}"
  else
    rm -f "${f}.tmp" 2>/dev/null
    # 若安装了 uchardet，尝试用其检测编码并转换
    if [ "$UCHARGE_DET_AVAILABLE" = true ]; then
      guessed=$(uchardet "$f" 2>/dev/null || true)
      guessed_lc=$(echo "$guessed" | tr '[:upper:]' '[:lower:]')
      if [ -n "$guessed" ] && iconv -f "$guessed" -t utf-8 "$f" -o "${f}.tmp" 2>/dev/null; then
        mv "${f}.tmp" "$f" && converted=true
        echo "Converted (${guessed} -> utf8): $f" | tee -a "${LOGFILE}"
      else
        rm -f "${f}.tmp" 2>/dev/null
      fi
    fi
  fi

  if [ "$converted" = false ]; then
    echo "转换失败（跳过，保留 .bak）：$f" | tee -a "${LOGFILE}"
    # 保持原样，继续下一个文件
  else
    count_converted=$((count_converted+1))
  fi
done

echo "完成: 需要转换 $count_need 个，已成功转换 $count_converted 个" | tee -a "${LOGFILE}"
echo "结束时间: $(date)" >> "${LOGFILE}"
echo "日志保存在 ${LOGFILE}"
