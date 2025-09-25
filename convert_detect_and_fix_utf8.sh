#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(pwd)"
BACKUP_DIR="$ROOT_DIR/encoding_backup_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

# 常用候选编码（优先级）：如果 file 能给出 charset，会把它放在首位尝试
FALLBACK_ENCODINGS=(GB2312 GBK GB18030 CP936 ISO-8859-1 WINDOWS-1252)

echo "当前目录：$ROOT_DIR"
echo "先把文件备份到：$BACKUP_DIR（建议在运行前也做 git commit）"
echo

converted=0
skipped=0
failed=0

# helper: 判断文件是否为合法 UTF-8（使用 iconv 尝试自编码）
is_valid_utf8() {
    local f="$1"
    if iconv -f UTF-8 -t UTF-8 "$f" >/dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# 遍历 .java 文件
find "$ROOT_DIR" -type f -name "*.java" | while IFS= read -r file; do
    # 跳过脚本自身和备份目录内文件
    [[ "$file" == "$BACKUP_DIR"* ]] && continue

    echo "处理: $file"

    # 先备份原文件（只备份一次）
    rel="$(realpath --relative-to="$ROOT_DIR" "$file")"
    backup_path="$BACKUP_DIR/$rel"
    mkdir -p "$(dirname "$backup_path")"
    cp -p "$file" "$backup_path"

    if is_valid_utf8 "$file"; then
        echo "  已是 UTF-8，跳过。"
        skipped=$((skipped+1))
        continue
    fi

    # 尝试 file -bi 获取 charset
    detected_charset="$(file -bi "$file" 2>/dev/null | sed -n 's/.*charset=//p' || true)"
    try_encodings=()
    if [[ -n "$detected_charset" ]]; then
        try_encodings+=("$detected_charset")
    fi
    # append fallback list (避免重复)
    for e in "${FALLBACK_ENCODINGS[@]}"; do
        already=0
        for x in "${try_encodings[@]}"; do [[ "$x" == "$e" ]] && already=1; done
        [[ $already -eq 0 ]] && try_encodings+=("$e")
    done

    success=0
    for enc in "${try_encodings[@]}"; do
        # iconv 对某些名称可能不识别，捕获错误
        if iconv -f "$enc" -t UTF-8 "$file" -o "${file}.utf8" 2>/dev/null; then
            mv "${file}.utf8" "$file"
            echo "  成功：从 '$enc' -> UTF-8"
            converted=$((converted+1))
            success=1
            break
        fi
    done

    if [[ $success -eq 0 ]]; then
        echo "  ⚠️ 无法自动转换：尝试的编码(${try_encodings[*]})均失败。原文已备份到 $backup_path"
        failed=$((failed+1))
    fi

done

echo
echo "完成：转换成功 $converted 个，跳过 $skipped 个（已是 UTF-8），失败 $failed 个（见备份）"
echo "备份目录：$BACKUP_DIR"
