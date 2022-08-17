import React, { useEffect, useState } from "react";
import { useSelector, useDispatch } from 'react-redux';
import Box from "@mui/material/Box";
import { Button, Grid } from "@mui/material";
import EducationForm from './EducationForm';
import { isArrayPresent } from '../../../utils/helperFunctions';
import postEducation from '../../../api/postEducation';

let initialData = {
  id: 0,
  highestDegree: '',
  institution: '',
  location: '',
  degree: '',
  major: '',
  completionDate: '',
  additionalInformation: ''
};

function Education() {
  const dispatch = useDispatch();
  const loginData = useSelector((state) => state.login);
  const educationData = useSelector((state) => state.education);

  const [educationSections, setEducationSections] = useState(educationData);

  useEffect(() => {
    if (!isArrayPresent(educationData)) {
      setEducationSections([{ ...initialData}]);
    }
  }, [educationData]);

  function handleSaveInformation(e) {
    e.preventDefault();
    // console.log("TODO: Submit data to backend!");
    dispatch(postEducation(educationSections, loginData?.accessToken));
  }

  function addMoreSections() {
    setEducationSections([...educationSections, { ...initialData, id: educationSections.length }])
  }



  return (
    <Box sx={{ width: "100%", textAlign: "left" }}>
      <form autoComplete="off" onSubmit={handleSaveInformation}>
        {isArrayPresent(educationSections) && educationSections.map((section) => {
          return <EducationForm section={section} educationSections={educationSections} setEducationSections={setEducationSections} key={section.id} />;
        })}

        <Grid container justifyContent={"flex-end"}>
          <Button variant="outlined" sx={{
            mt: "16px",
            backgroundColor: "#1666b6 !important",
            color: "white !important",
            fontWeight: "bold",
            boxShadow:
              "0px 3px 1px -2px rgb(0 0 0 / 20%), 0px 2px 2px 0px rgb(0 0 0 / 14%), 0px 1px 5px 0px rgb(0 0 0 / 12%)"
          }} onClick={addMoreSections}>Add more</Button>
        </Grid>
      </form>
    </Box>
  );
}

export default Education;
