import React, { useEffect, useState } from "react";
import { useSelector, useDispatch } from 'react-redux';
import FormControl from "@mui/material/FormControl";
import FormLabel from "@mui/material/FormLabel";
import Grid from "@mui/material/Grid";
import InputField from "../../../components/inputs/InputField";
import TextField from "../../../components/inputs/TextField";
import Box from "@mui/material/Box";
import { Button } from "@mui/material";
import EducationForm from './EducationForm';
import { isArrayPresent } from '../../../utils/helperFunctions';
import postEducation from '../../../api/postEducation';
import { v4 as uuidv4 } from 'uuid';

let initialData = {
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
      setEducationSections([{ ...initialData, id: uuidv4() }]);
    }
  }, [educationData]);

  useEffect(() => {
    console.log(educationSections);
    console.log(isArrayPresent(educationSections));
  }, [educationSections])

  function handleSaveInformation(e) {
    e.preventDefault();
    // console.log("TODO: Submit data to backend!");
    dispatch(postEducation(educationSections, loginData?.accessToken));
  }

  function addMoreSections() {
    setEducationSections([...educationSections, { ...initialData, id: uuidv4() }])
  }


  return (
    <Box sx={{ width: "100%", textAlign: "left" }}>
      <form autoComplete="off" onSubmit={handleSaveInformation}>
        {isArrayPresent(educationSections) && educationSections.map((section, index) => {
          return <EducationForm section={section} educationSections={educationSections} setEducationSections={setEducationSections} key={index} />;
        })}

        <Button variant="outlined" onClick={addMoreSections}>Add more</Button>
      </form>
    </Box>
  );
}

export default Education;
