module.exports = {
  env: {
    node: true,
    es2021: true,
    jest: true
  },
  extends: ["eslint:recommended", "plugin:prettier/recommended"],
  parserOptions: {
    ecmaVersion: 12
  },
  rules: {
    "no-console": "warn",
    "prettier/prettier": "error"
  }
};
