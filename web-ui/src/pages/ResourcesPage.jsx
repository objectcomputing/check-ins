import React, { useState } from "react";
import { Document, Page } from "react-pdf/dist/esm/entry.webpack";

import "./ResourcesPage.css";

const ResourcesPage = () => {
  const [numPages, setNumPages] = useState(null);
  const [pageNum, setPageNum] = useState(1);
  const [PDF, setPDF] = useState(null);
  let isPDL = false;

  const ociBlue = "#255aa8";
  const ociLightBlue = "#72c7d5";
  const ociOrange = "#feb672";

  const getPDF = async (name) => {
    try {
      const res = await fetch("/pdfs/" + name + ".pdf");
      if (res.ok) return res.blob();
      const message = await res.text();
      throw new Error(message);
    } catch (e) {
      console.error(e);
    }
  };

  const getPDFs = async (pdfArray) => {
    for (const tmp of pdfArray) {
      tmp.pdf = await getPDF(tmp.name);
    }
  };

  const teamMemberPDFs = [
    {
      color: ociBlue,
      name: "Expectations Discussion Guide for Team Members",
    },
    {
      color: ociBlue,
      name: "Expectations Worksheet",
    },
    {
      color: ociLightBlue,
      name: "Feedback Discussion Guide for Team Members",
    },
    {
      color: ociLightBlue,
      name: "Development Discussion Guide for Team Members",
    },
    {
      color: ociOrange,
      name: "Individual Development Plan",
    },
  ];

  getPDFs(teamMemberPDFs);

  const pdlPDFs = [
    {
      color: ociBlue,
      name: "Development Discussion Guide for PDLs",
    },
    {
      color: ociLightBlue,
      name: "Expectations Discussion Guide for PDLs",
    },
    {
      color: ociOrange,
      name: "Feedback Discussion Guide for PDLs",
    },
  ];

  getPDFs(pdlPDFs);

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

export default ResourcesPage;
