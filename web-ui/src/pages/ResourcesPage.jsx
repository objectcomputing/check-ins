import React, { useState } from "react";
import { Document, Page } from "react-pdf/dist/entry.webpack";
import myPDF from "../pdfs/Expectations Discussion Guide for Team Members.pdf";

const HomePage = () => {
  const [numPages, setNumPages] = useState(null);
  const [pageNum, setPageNum] = useState(1);

  const right = ">";
  const left = "<";

  const onDocumentLoadSuccess = ({ numPages }) => {
    setNumPages(numPages);
    console.log({ numPages });
  };

  const nextPage = (numPages) => {
    if (pageNum < numPages) {
      setPageNum(pageNum + 1);
    }
  };

  const prevPage = () => {
    if (pageNum > 1) {
      setPageNum(pageNum - 1);
    }
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        flexDirection: "column",
      }}
    >
      <h3>Professional Development @ OCI</h3>
      <Document file={myPDF} onLoadSuccess={onDocumentLoadSuccess}>
        <Page key={`page_${pageNum}`} pageNumber={pageNum} />
      </Document>
      <p>
        {pageNum > 1 && <button onClick={() => prevPage()}>{left}</button>}
        Page {pageNum} of {numPages}
        {pageNum < numPages && (
          <button onClick={() => nextPage(numPages)}>{right}</button>
        )}
      </p>
    </div>
  );
};

export default HomePage;
