import React, { useState, useEffect } from "react";
import "./DocumentSigningPage.css";
import getSigner from "../api/signer.js";

const DocumentSigningPage = () => {
  const [document, setDocument] = useState(null);

  useEffect(async () => {
    let res = await getSigner();
    let document;
    if (res && res.payload) {
      document = res.payload.data && !res.error ? res.payload.data : undefined;
      if (document) {
        setDocument(document);
      }
    }
  }, [getSigner]);

  return (
    <div>
      <center>
        <h1>Internal Document Signing</h1>
        {document.status === "si" || document.status === "sd" ? "Signed Baby!" : "Not Signed. :("}
      </center>
    </div>
  );
};

// const [signer, setSigner] = useState([]);

// useEffect(() => {
//   const fetchData = async () => {
//     // const xhr = new XMLHttpRequest();
//     // xhr.open('GET', 'http://localhost:8080/signer');
//     // xhr.setRequestHeader('Content-Type', 'application/json');

//     // xhr.send();

//     const result = await fetch('http://localhost:8080/signer',{
//       'Access-Control-Allow-Credentials': true
//     });
//     const jsonResult = await result.json();
//     console.log(JSON.stringify(jsonResult));

//     setSigner(result);
//   }

//   fetchData();
// }, [])
export default DocumentSigningPage;
