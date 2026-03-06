#!/usr/bin/env bash

set -u

REMOTE_NAME="${1:-origin}"
REMOTE_URL="${2:-git@github.com:li-weil/deedm_alpha.git}"
SSH_TARGET="${3:-git@github.com}"
SSH_HOST="${SSH_TARGET#*@}"

run_check() {
  local title="$1"
  shift

  echo
  echo "== ${title} =="
  echo "+ $*"
  "$@"
  local status=$?
  echo "[exit=${status}]"
  return 0
}

echo "GitHub SSH connectivity check"
echo "remote_name=${REMOTE_NAME}"
echo "remote_url=${REMOTE_URL}"
echo "ssh_target=${SSH_TARGET}"
echo "ssh_host=${SSH_HOST}"

run_check "Proxy Environment" env
run_check "Git Remote" git remote -v
run_check "SSH Directory" ls -la "${HOME}/.ssh"
run_check "DNS Resolution" getent hosts "${SSH_HOST}"
run_check "TCP Port 22" nc -vz "${SSH_HOST}" 22
run_check "GitHub SSH Auth" ssh -T -o StrictHostKeyChecking=accept-new -o ConnectTimeout=10 "${SSH_TARGET}"
run_check "Remote Refs" git ls-remote "${REMOTE_URL}"

