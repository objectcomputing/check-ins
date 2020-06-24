import React, { useState } from "react";
import { Document, Page } from "react-pdf/dist/entry.webpack";
import DevGuide from "../pdfs/Development Discussion Guide for Team Members.pdf";
import ExpectationsGuide from "../pdfs/Expectations Discussion Guide for Team Members.pdf";
import ExpectationsWorksheet from "../pdfs/Expectations Worksheet.pdf";
import FeedbackGuide from "../pdfs/Feedback Discussion Guide for Team Members.pdf";
import IndividualDevPlan from "../pdfs/Individual Development Plan .pdf";
import "./ResourcesPage.css";

const HomePage = () => {
  const [numPages, setNumPages] = useState(null);
  const [pageNum, setPageNum] = useState(1);
  const [PDF, setPDF] = useState(null);

  const pdfs = [
    { name: "Development Discussion Guide", pdf: DevGuide },
    { name: "Expectations Discussion Guide", pdf: ExpectationsGuide },
    { name: "Expectations Worksheet", pdf: ExpectationsWorksheet },
    { name: "Feedback Discussion Guide", pdf: FeedbackGuide },
    { name: "Individual Development Plan", pdf: IndividualDevPlan },
  ];

  const onDocumentLoadSuccess = ({ numPages }) => {
    setNumPages(numPages);
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

  const ChoosePDF = () => {
    return pdfs.map((pdf) => (
      <div
        className="blue-button"
        key={pdf.name}
        onClick={() => setPDF(pdf.pdf)}
      >
        {pdf.name}
      </div>
    ));
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
      <br />
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          marginBottom: "20px",
        }}
      >
        <ChoosePDF />
      </div>
      {PDF && (
        <div>
          <p>
            {pageNum > 1 && (
              <button className="blue-button" onClick={() => prevPage()}>
                Prev
              </button>
            )}
            Page {pageNum} of {numPages}
            {pageNum < numPages && (
              <button
                className="blue-button"
                onClick={() => nextPage(numPages)}
              >
                Next
              </button>
            )}
          </p>
          <Document file={PDF} onLoadSuccess={onDocumentLoadSuccess}>
            <Page key={`page_${pageNum}`} pageNumber={pageNum} />
          </Document>
          <p>
            {pageNum > 1 && (
              <button className="blue-button" onClick={() => prevPage()}>
                Prev
              </button>
            )}
            Page {pageNum} of {numPages}
            {pageNum < numPages && (
              <button
                className="blue-button"
                onClick={() => nextPage(numPages)}
              >
                Next
              </button>
            )}
          </p>
        </div>
      )}
    </div>
  );
};

export default HomePage;
