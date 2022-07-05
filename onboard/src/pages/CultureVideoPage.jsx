import React from "react";
import "./CultureVideoPage.css";
function CultureVideoPage(props) {
  
  return (
    <div>
      <center>
        <h1>At OCI We Care</h1>
      </center>
      <center>
        <iframe
          width="1280"
          height="720"
          src="https://www.youtube.com/embed/D7Ka15wQL5U"
          title="YouTube video player"
          frameBorder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowFullScreen
        ></iframe>
      </center>
    </div>
  );
}

export default CultureVideoPage;
