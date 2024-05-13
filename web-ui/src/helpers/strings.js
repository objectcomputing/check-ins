export const titleCase = text =>
  text
    .replace(/_/g, ' ')
    .replace(
      /\w\S*/g,
      word => word.charAt(0).toUpperCase() + word.substr(1).toLowerCase()
    );
