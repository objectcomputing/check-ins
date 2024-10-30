import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react-swc';

// https://vitejs.dev/config/
export default defineConfig({
  base: '/',
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/setupTests.js',
    css: false,
    reporters: ['verbose'],
    coverage: {
      reporter: ['text', 'json', 'html'],
      include: ['src/**/*'],
      exclude: []
    }
  },
  optimizeDeps: {
    include: ['@mui/material/Tooltip']
  },
  build: {
    rollupOptions: {
      input: {
        main: 'index.html',
        externalRecipient: 'index_external_recipient.html'
      }
    }
  }
});
