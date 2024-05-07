export const titleCase = text =>
  text.replace(
    /\w\S*/g,
    word => word.charAt(0).toUpperCase() + word.substr(1).toLowerCase()
  );
