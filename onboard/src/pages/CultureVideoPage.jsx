import React from "react";
import "./CultureVideoPage.css";
function CultureVideoPage(props) {
  
  return (
    <div>
      <center>
        <iframe
          align="center"
          min-width="480"
          min-height="360"
          width="1080"
          height="720"
          src="https://www.youtube-nocookie.com/embed/D7Ka15wQL5U"
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
