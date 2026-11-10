$ErrorActionPreference = "Stop"

$repoUrl = "https://github.com/therajsiddharth/Campus-Lost-and-Found-Management-System.git"

Write-Host "Preparing Campus Lost & Found project for GitHub..." -ForegroundColor Cyan

if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    throw "Git is not installed. Install Git for Windows, reopen PowerShell, and run this script again."
}

if (-not (Test-Path ".git")) {
    git init
    if ($LASTEXITCODE -ne 0) { throw "git init failed." }
}

git branch -M main
if ($LASTEXITCODE -ne 0) { throw "Could not set branch to main." }

$remote = git remote get-url origin 2>$null
if ($LASTEXITCODE -eq 0 -and $remote -ne $repoUrl) {
    git remote set-url origin $repoUrl
    if ($LASTEXITCODE -ne 0) { throw "Could not update origin remote." }
}
elseif ($LASTEXITCODE -ne 0) {
    git remote add origin $repoUrl
    if ($LASTEXITCODE -ne 0) { throw "Could not add origin remote." }
}

git add .
if ($LASTEXITCODE -ne 0) { throw "git add failed." }

git diff --cached --quiet
if ($LASTEXITCODE -eq 1) {
    git commit -m "Initial project upload: Campus Lost and Found Management System"
    if ($LASTEXITCODE -ne 0) { throw "Commit failed. Configure your Git name/email and retry." }
}
elseif ($LASTEXITCODE -gt 1) {
    throw "Could not inspect staged changes."
}
else {
    Write-Host "No new changes to commit." -ForegroundColor Yellow
}

Write-Host "Pushing to GitHub. Git may open a browser window for authentication." -ForegroundColor Cyan
git push -u origin main
if ($LASTEXITCODE -ne 0) {
    throw "Push failed. Complete GitHub sign-in in Git Credential Manager and rerun this script."
}

Write-Host "Upload completed. Verify your files at:" -ForegroundColor Green
Write-Host "https://github.com/therajsiddharth/Campus-Lost-and-Found-Management-System"
