import React, { useState, useEffect } from "react";
import getEmbeddedUrl from "../api/signrequest_embed";

const EmbedRequest = () => {
  const [embeddedUrl, setEmbeddedUrl] = useState(false);

  useEffect(() => {
    let ignore = false;

    async function getData() {
      let res = await getEmbeddedUrl();
      let signRequestUrl;
      if (res && res.payload) {
        signRequestUrl =
          res?.payload?.data && !res.error ? res.payload.data : undefined;
        if (signRequestUrl) {
          setEmbeddedUrl(res.payload.data);
        }
      }
    }

    if (!ignore) {
      getData();
    }
    return () => {
      ignore = true;
    };
  }, []);

  return (
    <div>
      <center>
        <h1>Redirecting to the Embedded Form</h1>
      </center>
      <div id="content">
        <iframe
          className="iFrameWrapper"
          src={embeddedUrl}
          title="Test Embedded Sign Request Form"
        ></iframe>
      </div>
    </div>
  );
};

export default EmbedRequest;
