import globals from 'globals';
import reactHooksPlugin from 'eslint-plugin-react-hooks';
import reactPlugin from 'eslint-plugin-react';
import vitestPlugin from 'eslint-plugin-vitest';
import tseslint from 'typescript-eslint';

/**
 * @type {import('eslint').Linter.FlatConfig[]}
 */
export default [
  {
    files: ['src/**/*.js', 'src/**/*.jsx', 'src/**/*.ts', 'src/**/*.tsx'],
    languageOptions: {
      parser: tseslint.parser,
      parserOptions: {
        ecmaFeatures: {
          jsx: true
        }
      },
      globals: {
        ...globals.browser,
        ...globals.node,
        ...vitestPlugin.environments.env.globals,
        HttpResponse: false,
        snapshot: false,
        waitForSnapshot: false
      }
    },
    plugins: {
      react: reactPlugin,
      'react-hooks': reactHooksPlugin,
      '@typescript-eslint': tseslint.plugin,
      vitest: vitestPlugin
    },
    rules: {
      // Override default rules here.
      'react-hooks/exhaustive-deps': 'warn',
      ...vitestPlugin.configs.recommended.rules,
      'vitest/expect-expect': 'off'
    }
  }
];
