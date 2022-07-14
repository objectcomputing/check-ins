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
          src=""
          title="Test Embedded Sign Request Form"
        ></iframe>
      </div>
    </div>
  );
};

export default EmbedRequest;
