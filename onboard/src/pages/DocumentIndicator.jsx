import React, { useState, useEffect } from "react";
import getDocuments from "../api/signrequest_documents.js";

const DocumentIndicator = () => {
  const [documentArr, setDocumentArr] = useState([]);

  useEffect(() => {
    async function getData() {
      let res = await getDocuments();
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
