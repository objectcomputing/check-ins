import React from "react";
import "./TestEmbeddedSigningPage.css";

const EmbedRequest = () => {
  return (
    <div className="wrapContent">
      <center>
        <h1>Please Sign the Form Below</h1>
      </center>
      <div id="content">
        <iframe
          className="iFrameWrapper"
          src="https://brandontest.signrequest.com/r/document/7d3f0229-8e39-4c9b-b984-fbe92f0815cc/843be4495efc4da092e00be04bac3c262345dc9e49cdeadc298dc2b2e4a4a85d/"
          title="Test Embedded Sign Request Form"
        ></iframe>
      </div>
    </div>
  );
};

export default EmbedRequest;
