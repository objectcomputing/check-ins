import React from "react";

const HomePage = () => (
  <div
    style={{
      display: "flex",
      justifyContent: "center",
      flexDirection: "column",
    }}
  >
    <h2>Quarterly Development Check-ins</h2>
    <p>
      The role of Professional Development Lead is vital to the continued growth
      of our organization and our team members. Thank you for your time and
      dedication to our team!
    </p>
    <p>
      You can submit any Check-in notes or other relevant documentation that you
      would like to persist for future PDLs or for consideration in merit
      discussions via the form below. Access to files uploaded here will be
      restricted to the team member, as well as those who require them to
      perform their role (HR personnel, PDLs, etc).
    </p>
    <p>
      If you should require a copy of any of the documents that you have
      submitted here, please email&nbsp;
      <a href="mailto:hr@objectcomputing.com">hr@objectcomputing.com</a>
    </p>

    <p>
      Please note that the following form can only upload one file at a time
      (max size of 100MB).
    </p>
    <form action="/upload" method="post" enctype="multipart/form-data">
      <fieldset id="upload-fs" className="fieldset">
        <input
          type="file"
          name="file"
          id="file"
          className="uploader"
          onchange="updateName();"
        />
        <label for="file" id="filesName">
          Choose a file
        </label>
      </fieldset>
      <p>
        <button type="submit" name="submit">
          Upload
        </button>
      </p>

      <div id="file-upload-filename"></div>
    </form>
  </div>
);

export default HomePage;
