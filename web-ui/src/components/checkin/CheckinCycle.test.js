import React from "react";
import CheckinCycle from "./CheckinCycle";

const style = { backgroundColor: "red" };
it("renders correctly", () => {
  snapshot(<CheckinCycle style={style} />);
});
