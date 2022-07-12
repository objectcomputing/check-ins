import React, { useState, useEffect } from "react";

const DocumentIndicator = (props) => {
  const [documentArr, setDocumentArr] = useState([]);

  useEffect(() => {
    async function getData() {
      let res = await props.documentRequest;
      let document;
      if (res && res.payload) {
        document =
          res?.payload?.data && !res.error ? res.payload.data : undefined;
        if (document) {
          setDocumentArr([...document.results]);
        }
      }
    }
    getData();
  }, []);

  return (
    <div>
      <center>
        {documentArr.map((e) => (
          <p key={e.uuid}>
            {e.name + " is "}
            {e.status === "sd" || e.status === "si"
              ? "signed"
              : "not signed yet"}
          </p>
        ))}
      </center>
    </div>
  );
};
export default DocumentIndicator;
