import React from "react";
import SentimentIcon from "./SentimentIcon";

it("renders correctly", () => {
  snapshot(
    <SentimentIcon sentimentScore={0.8}/>
  );
});