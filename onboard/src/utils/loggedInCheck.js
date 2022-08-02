const checkExpirationTime = (expirationDate, currentDate) => {
  // Get time of current date
  let now = currentDate.getTime();

  //   Check the expiration date's time
  let endDate = expirationDate.getTime();
  //   Get the difference between times

  let diff = endDate - now;

  // As long as there is still time left before expiration, parse and return the hours,
  // minutes and seconds left. (This is unused currently but could be useful in the future.)
  if (diff > 0) {
    //   Calculate the hours left
    let hours = Math.floor(diff / 1000 / 60 / 60);
    if (hours < 0) hours = hours + 24;

    // Calculate the minutes left
    diff -= hours * (1000 * 60 * 60);
    const minutes = Math.floor(diff / 1000 / 60);

    // Calculate the seconds left
    diff -= minutes * (1000 * 60);
    const seconds = Math.floor(diff / 1000);

    // Return the values as a JSON object for reference
    return { time: diff, hours: hours, minutes: minutes, seconds: seconds };
    // If there is no time left before expiration, return time: 0, which will be used by the next function.
  } else {
    return { time: 0 };
  }
};

// Check whether the user is logged in based on access token and expiration time
export const loggedInCheck = (loginData) => {
  // Set the current time to empty to initialize
  let currentTime = {};

  // Check for an expiration within loginData, a store parameter passed into the function
  if (loginData?.expiration) {
    // Set the expiration date parameter for checkExpirationTime() to consume
    // This is done by creating a new Date() using the expiration data passed in * 1000
    let expirationDate = new Date(loginData.expiration * 1000);
    // console.log("This is the expirationDate: " + expirationDate);
    // Set the current date parameter for checkExpirationTime() to consume
    let currentDate = new Date();
    // Reset current time to the checkExpirationTime function, and pass in the parameters above
    currentTime = checkExpirationTime(expirationDate, currentDate);
  }

  // Now, check for the loginData access token and whether the currentTime const is more than 0.
  // Without this, the user is definitely not logged in.

  return Boolean(loginData?.accessToken && currentTime?.time > 0);
};
