import React from "react";
import Notes from "./Note";
// import { render, fireEvent, screen } from "@testing-library/react";

let checkin = {
  id: "3a1906df-d45c-4ff5-a6f8-7dacba97ff1a",
  checkinid: "bf9975f8-a5b2-4551-b729-afd56b49e2cc",
  createdbyid: "5425d835-dcd1-4d91-9540-200c06f18f28",
  description: "updated string",
};

it("renders correctly with checkin", () => {
  snapshot(<Notes checkin={checkin} />);
});
