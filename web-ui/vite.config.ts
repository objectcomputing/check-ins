import { defineConfig } from "vitest/config"
import react from "@vitejs/plugin-react-swc"

// https://vitejs.dev/config/
export default defineConfig({
  base: "/",
  plugins: [react()],
  optimizeDeps: {
    exclude: ['@nivo/bar', '@nivo/line', '@nivo/pie', '@nivo/tooltip', '@nivo/legends', '@nivo/scales', '@nivo/colors', '@nivo/core', '@nivo/axes', '@nivo/annotations', '@nivo/stream', '@nivo/sankey', '@nivo/waffle', '@nivo/sunburst', '@nivo/parallel', '@nivo/heatmap', '@nivo/calendar', '@nivo/radar', '@nivo/treemap', '@nivo/bullet', '@nivo/chord', '@nivo/voronoi'],
  },
  test: {
    globals: true,
    environment: "jsdom",
    setupFiles: "./src/setupTests.js",
    css: true,
    reporters: ["verbose"],
    coverage: {
      reporter: ["text", "json", "html"],
      include: ["src/**/*"],
      exclude: [],
    }
  },
  optimizeDeps: {
    include: ['@mui/material/Tooltip'],
  },
})
