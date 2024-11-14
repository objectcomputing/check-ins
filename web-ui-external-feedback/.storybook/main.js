module.exports = {
  "stories": ["../src/**/*.mdx", "../src/**/*.stories.@(js|jsx|ts|tsx)"],
  "staticDirs": ["../public"], //👈 Configures the static asset folder in Storybook
  "core": {
    "disableTelemetry": true, // 👈 Disables telemetry
  },

  "addons": [
    "@storybook/addon-links",
    "@storybook/addon-essentials",
    "@storybook/preset-create-react-app"
  ],

  "framework": {
    "name": "@storybook/react-webpack5",
    "options": {}
  },

  "docs": {
    "autodocs": true
  }
}
