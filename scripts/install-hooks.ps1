$ErrorActionPreference = 'Stop'
# Activa la red de verificación local de citas-api (pre-commit: escaneo de secretos + pruebas).
git config core.hooksPath .githooks
Write-Output 'Hooks de citas-api configurados (core.hooksPath = .githooks).'
