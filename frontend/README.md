# Quizzes FrontEnd

Requirements:
- Node: `v18.14.2`
- Yarn: `1.22.19`

## Setup

Install NVM: https://github.com/coreybutler/nvm-windows/releases/download/1.1.10/nvm-setup.exe

Install Node:
```bash
nvm list
nvm install 18.14.2
nvm use 18.14.2
```

Install yarn:
```bash
npm install --global yarn
```

Clone project:
```bash
git clone <gitlab url>
```

Install project:
```bash
yarn
# or
yarn install
```

Run project for dev:
```bash
yarn start
```

Build exe:
```bash
yarn electron:package:win
```

In VSCode, install Prettier (esbenp.prettier-vscode) and to enable format on save, add the following to your settings.json (CTRL + P -> Preferences: Open User Settings (JSON))):
```json
"editor.defaultFormatter": "esbenp.prettier-vscode",
"editor.formatOnSave": true,
```

For only .js, .jsx and .html files:
```json
"[html]": {
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.formatOnSave": true,
},

"[javascript]": {
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.formatOnSave": true,
},

"[javascriptreact]": {
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.formatOnSave": true,
},
```
