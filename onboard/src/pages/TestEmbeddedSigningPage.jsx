import React from "react";
import "./TestEmbeddedSigningPage.css";

const EmbedRequest = ({ iframeUrl }) => {
  return (
    <div className="wrapContent">
      <center>
        <h1>Please Sign the Form Below</h1>
      </center>
      <div id="content">
        {iframeUrl && (
          <iframe
            className="iFrameWrapper"
            src={iframeUrl}
            title="Test Embedded Sign Request Form"
          ></iframe>
        )}
      </div>
    </div>
  );
};

export default EmbedRequest;
