import React, { useState } from "react";
import { Document, Page } from "react-pdf/dist/entry.webpack";

import DevGuideTeam from "../pdfs/Development Discussion Guide for Team Members.pdf";
import ExpectationsGuideTeam from "../pdfs/Expectations Discussion Guide for Team Members.pdf";
import ExpectationsWorksheetTeam from "../pdfs/Expectations Worksheet.pdf";
import FeedbackGuideTeam from "../pdfs/Feedback Discussion Guide for Team Members.pdf";
import IndividualDevPlanTeam from "../pdfs/Individual Development Plan .pdf";
import DevGuidePDL from "../pdfs/Development Discussion Guide for PDLs.pdf";
import ExpectationsGuidePDL from "../pdfs/Expectations Discussion Guide for PDLs.pdf";
import FeedbackGuidePDL from "../pdfs/Feedback Discussion Guide for PDLs.pdf";

import "./ResourcesPage.css";

const HomePage = () => {
  const [numPages, setNumPages] = useState(null);
  const [pageNum, setPageNum] = useState(1);
  const [PDF, setPDF] = useState(null);
  const [isPDL, setIsPDL] = useState(false);

  const ociBlue = "#255aa8";
  const ociLightBlue = "#72c7d5";
  const ociOrange = "#feb672";

  const teamMemberPDFs = [
    {
      color: ociBlue,
      name: "Expectations Discussion Guide",
      pdf: ExpectationsGuideTeam,
    },
    {
      color: ociBlue,
      name: "Expectations Worksheet",
      pdf: ExpectationsWorksheetTeam,
    },
    {
      color: ociLightBlue,
      name: "Feedback Discussion Guide",
      pdf: FeedbackGuideTeam,
    },
    {
      color: ociLightBlue,
      name: "Development Discussion Guide",
      pdf: DevGuideTeam,
    },
    {
      color: ociOrange,
      name: "Individual Development Plan",
      pdf: IndividualDevPlanTeam,
    },
  ];

  const pdlPDFs = [
    {
      color: ociBlue,
      name: "Development Discussion Guide",
      pdf: DevGuidePDL,
    },
    {
      color: ociLightBlue,
      name: "Expectations Discussion Guide",
      pdf: ExpectationsGuidePDL,
    },
    {
      color: ociOrange,
      name: "Feedback Discussion Guide",
      FeedbackGuidePDL,
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
    let pdfMap = isPDL ? pdlPDFs : teamMemberPDFs;

    return pdfMap.map((pdf) => (
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
  const handlePrint = (event) => {
    event.preventDefault();
    window.open(PDF, "PRINT", "height=700,width=900");
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
          <button
            className="custom-button"
            onClick={handlePrint}
            style={{ backgroundColor: ociBlue, marginBottom: "50px" }}
          >
            Print pdf
          </button>
        </div>
      )}
    </div>
  );
};

export default HomePage;
