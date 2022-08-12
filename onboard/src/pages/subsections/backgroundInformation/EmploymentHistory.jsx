import React, { useEffect, useState } from "react";
import { useSelector, useDispatch } from 'react-redux';
import Box from "@mui/material/Box";
import { Button, Grid } from "@mui/material";
import EmploymentHistoryForm from './EmploymentHistoryForm';
import { isArrayPresent } from '../../../utils/helperFunctions';
import postJobHistory from '../../../api/postJobHistory';

let initialData = {
  id: 0,
  company: '',
  companyAddress: '',
  jobTitle: '',
  startDate: '',
  endDate: '',
  reason: ''
};

function JobHistory() {
  const dispatch = useDispatch();
  const loginData = useSelector((state) => state.login);
  const jobHistoryData = useSelector((state) => state.jobhistory);

  const [jobHistorySections, setjobHistorySections] = useState(jobHistoryData);

  useEffect(() => {
    if (!isArrayPresent(jobHistoryData)) {
      setjobHistorySections([{ ...initialData}]);
    }
  }, [jobHistoryData]);


  function handleSaveInformation(e) {
    e.preventDefault();
    // console.log("TODO: Submit data to backend!");
    dispatch(postJobHistory(jobHistorySections, loginData?.accessToken));
  }

  function addMoreSections() {
    setjobHistorySections([...jobHistorySections, { ...initialData, id: jobHistorySections.length }])
  }


  return (
    <Box sx={{ width: "100%", textAlign: "left" }}>
      <form autoComplete="off" onSubmit={handleSaveInformation}>
        {isArrayPresent(jobHistorySections) && jobHistorySections.map((section) => {
          return <EmploymentHistoryForm section={section} jobHistorySections={jobHistorySections} setJobHistorySections={setjobHistorySections} key={section.id} />;
        })}

        <Grid container justifyContent={"flex-end"}>
          <Button variant="outlined" sx={{
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

export default JobHistory;

