import React, { useState, useEffect } from "react";
import getEmbeddedURL from "../api/signrequest_embed";

const EmbedRequest = () => {
  const [embedUrl, setEmbedUrl] = useState([]);

  useEffect(() => {
    async function getData() {
      let res = await getEmbeddedURL();
      let url;
      if (res && res.payload) {
        url = res?.payload?.data && !res.error ? res.payload.data : undefined;
        if (url) {
          setEmbedUrl(url);
        }
      }
    }
    getData();
  });

  return (
    <div>
      <center>
        <h1>Please Sign the Form Below</h1>
      </center>
      <div id="content">
        <iframe
          className="iFrameWrapper"
          src={embedUrl}
          //src="https://www.youtube.com/embed/cWDJoK8zw58"
          title="Test Embedded Sign Request Form"
        ></iframe>
      </div>
    </div>
  );
};

export default EmbedRequest;
