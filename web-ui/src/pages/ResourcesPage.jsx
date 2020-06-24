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

  const ociBlue = "#255aa8";
  const ociLightBlue = "#72c7d5";
  const ociOrange = "#feb672";

  const pdfs = [
    {
      color: ociBlue,
      name: "Expectations Discussion Guide",
      pdf: ExpectationsGuide,
    },
    {
      color: ociBlue,
      name: "Expectations Worksheet",
      pdf: ExpectationsWorksheet,
    },
    {
      color: ociLightBlue,
      name: "Feedback Discussion Guide",
      pdf: FeedbackGuide,
    },
    {
      color: ociLightBlue,
      name: "Development Discussion Guide",
      pdf: DevGuide,
    },
    {
      color: ociOrange,
      name: "Individual Development Plan",
      pdf: IndividualDevPlan,
    },
  ];

  const onDocumentLoadSuccess = ({ numPages }) => {
    setNumPages(numPages);
    setPageNum(1);
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
        className="custom-button"
        key={pdf.name}
        onClick={() => {
          setPDF(pdf.pdf);
        }}
        style={{ backgroundColor: pdf.color }}
      >
        {pdf.name}
      </div>
    ));
  };

  const ChangePage = () => {
    return (
      <p>
        {pageNum > 1 && (
          <button
            className="custom-button"
            onClick={() => prevPage()}
            style={{ backgroundColor: ociBlue }}
          >
            Prev
          </button>
        )}
        Page {pageNum} of {numPages}
        {pageNum < numPages && (
          <button
            className="custom-button"
            onClick={() => nextPage(numPages)}
            style={{ backgroundColor: ociBlue }}
          >
            Next
          </button>
        )}
      </p>
    );
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
      <div className="pdf-container">
        <ChoosePDF />
      </div>
      {PDF && (
        <div>
          <ChangePage />
          <Document file={PDF} onLoadSuccess={onDocumentLoadSuccess}>
            <Page key={`page_${pageNum}`} pageNumber={pageNum} />
          </Document>
          <ChangePage />
        </div>
      )}
    </div>
  );
};

export default HomePage;
