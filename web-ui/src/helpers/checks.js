/**
 * Full check for whether an array actually exists or is empty, etc
 * @param arr - an array
 * @returns a boolean
 */

export const isArrayPresent = arr => Array.isArray(arr) && arr.length;

/**
 * If a parameter is found in an object within an array, return the array with just that object.
 * @param arr - an array
 * @param value - a value
 * @param key - an optional key with which to search
 * @returns an array
 */

export function filterObjectByValOrKey(arr, value, key) {
  return arr.filter(
    key
      ? a => a[key].indexOf(value) > -1
      : a => Object.keys(a).some(k => a[k] === value)
  );
}

/**
 * Sort Search Results for team members by Skill, Level & Name
 * @param searchResults - an array of team members
 * @returns a sorted array
 */
export function sortMembersBySkill(searchResults) {
  const skillLevelsOrder = [
    'EXPERT',
    'ADVANCED',
    'INTERMEDIATE',
    'NOVICE',
    'INTERESTED'
  ];

  if (isArrayPresent(searchResults)) {
    const sortedArray = searchResults.sort((a, b) => {
      // If number of skills is not the same, sort by number of skills
      if (a.skills.length !== b.skills.length) {
        return b.skills.length - a.skills.length;
      } else {
        // If the number of skill are the same, sort by total skill level
        const scoreA = a.skills.reduce((acc, skill) => {
          const level = skillLevelsOrder.indexOf(skill.level);
          return acc + level;
        }, 0);
        const scoreB = b.skills.reduce((acc, skill) => {
          const level = skillLevelsOrder.indexOf(skill.level);
          return acc + level;
        }, 0);

        if (scoreA !== scoreB) {
          return scoreA - scoreB;
        } else {
          // If skills and levels are the same, sort alphabetically by name
          return a.name.localeCompare(b.name);
        }
      }
    });
    return sortedArray;
  } else {
    return [];
  }
}
