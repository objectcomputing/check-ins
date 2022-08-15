import React, { useState } from "react";
import FormControl from "@mui/material/FormControl";
import Grid from "@mui/material/Grid";
import InputField from "../../../components/inputs/InputField";
import TextField from "../../../components/inputs/TextField";
import { Divider } from "@mui/material";

function EmploymentHistory({ section, jobHistorySections, setJobHistorySections }) {

    const [companyHelper, setCompanyHelper] = useState("");
    const [companyAddressHelper, setCompanyAddressHelper] = useState("");
    const [jobTitleHelper, setJobTitleHelper] = useState("");
    const [startDateHelper, setStartDateHelper] = useState("");
    const [endDateHelper, setEndDateHelper] = useState("");
    const [reasonHelper, setReasonHelper] = useState("");

    const [companyError, setCompanyError] = useState(false);
    const [companyAddressError, setCompanyAddressError] = useState(false);
    const [jobTitleError, setJobTitleError] = useState(false);
    const [startDateError, setStartDateError] = useState(false);
    const [endDateError, setEndDateError] = useState(false);
    const [reasonError, setReasonError] = useState(false);

    function adjustState(name, val) {
        let newArr = [];
        jobHistorySections.forEach((jSection) => {
            if (jSection.id === section.id) {
                newArr.push({ ...section, [name]: val });
            } else {
                newArr.push(jSection);
            }
        })
        return newArr;
    }

    function handleChange(event) {
        const e = event;
        const val = e.target.value;
        const name = e.target.name;

        let newState = adjustState(name, val);
        setJobHistorySections(newState);

        if (name === "company") {
            if (val.length > 0) {
                setCompanyError(false);
                setCompanyHelper("");
            } else {
                setCompanyError(true);
                setCompanyHelper(
                    "Please enter in the name of the company you previously worked for"
                );
            }
        } else if (name === "companyAddress") {
            if (val.length > 0) {
                setCompanyAddressError(false);
                setCompanyAddressHelper("");
            } else {
                setCompanyAddressError(true);
                setCompanyAddressHelper(
                    "Please enter in the address of the company you previously worked for"
                );
            }
        } else if (name === "jobTitle") {
            if (val.length > 0) {
                setJobTitleError(false);
                setJobTitleHelper("");
            } else {
                setJobTitleError(true);
                setJobTitleHelper(
                    "Please enter in the title of the job you had previously"
                );
            }
        } else if (name === "startDate") {
            if (val.length === 0) {
                setStartDateError(true);
                setStartDateHelper("Please enter in the start date of your previous job");
            }
            else {
                setStartDateError(false);
                setStartDateHelper("");
            }
        } else if (name === "endDate") {
            if (val.length === 0) {
                setEndDateError(true);
                setEndDateHelper("Please enter in the end date of your previous job");
            }
            else {
                setEndDateError(false);
                setEndDateHelper("");
            }
        } else if (name === "reason") {
            if (val.length > 0) {
                setReasonError(false);
                setReasonHelper("");
            } else {
                setReasonError(true);
                setReasonHelper(
                    "Please enter in your reason for leaving your previous job"
                );
            }
        }
    }

    return (
        <div>
        {section.id !== 0 && ( <Divider variant="middle" sx={{ mt: "24px"}}/> )}
        <Grid
            container
            rowSpacing={1}
            columnSpacing={{ xs: 1, sm: 2, md: 3 }}
            sx={{ marginTop: 3 }}
        >
            <Grid item xs={12} sm={12} md={12} lg={6}>
                <FormControl
                    sx={{
                        marginTop: 3,
                        marginBottom: 1,
                        marginLeft: 3,
                        width: "90%",
                        maxWidth: "500px",
                    }}
                >
                    <InputField
                        title={"Company"}
                        id="company"
                        value={section.company}
                        autoFocus={true}
                        error={companyError}
                        onChangeHandler={handleChange}
                        helperMessage={companyHelper}
                        label="Company"
                        type="text"
                    ></InputField>
                </FormControl>
            </Grid>
            <Grid item xs={12} sm={12} md={12} lg={6}>
                <FormControl
                    sx={{
                        marginTop: 3,
                        marginBottom: 1,
                        marginLeft: 3,
                        width: "90%",
                        maxWidth: "500px",
                    }}
                >
                    <InputField
                        title={"Company Address"}
                        id="companyAddress"
                        value={section.companyAddress}
                        error={companyAddressError}
                        onChangeHandler={handleChange}
                        helperMessage={companyAddressHelper}
                        label="Company Address"
                        type="text"
                    ></InputField>
                </FormControl>
            </Grid>
            <Grid item xs={12} sm={12} md={12} lg={6}>
                <FormControl
                    sx={{
                        marginTop: 3,
                        marginBottom: 1,
                        marginLeft: 3,
                        width: "90%",
                        maxWidth: "500px",
                    }}
                >
                    <InputField
                        title={"Job Title"}
                        id="jobTitle"
                        value={section.jobTitle}
                        error={jobTitleError}
                        onChangeHandler={handleChange}
                        helperMessage={jobTitleHelper}
                        label="Job Title"
                        type="text"
                    ></InputField>
                </FormControl>
            </Grid>
            <Grid item xs={12} sm={12} md={12} lg={6}>
                <FormControl
                    sx={{
                        marginTop: 3,
                        marginBottom: 1,
                        marginLeft: 3,
                        width: "90%",
                        maxWidth: "500px",
                    }}
                >
                    <InputField
                        title={"Start Date"}
                        id="startDate"
                        value={section.startDate}
                        error={startDateError}
                        onChangeHandler={handleChange}
                        helperMessage={startDateHelper}
                        label="Start Date"
                        type="date"
                    ></InputField>
                </FormControl>
            </Grid>
            <Grid item xs={12} sm={12} md={12} lg={6}>
                <FormControl
                    sx={{
                        marginTop: 3,
                        marginBottom: 1,
                        marginLeft: 3,
                        width: "90%",
                        maxWidth: "500px",
                    }}
                >
                    <InputField
                        title={"End Date"}
                        id="endDate"
                        value={section.endDate}
                        error={endDateError}
                        onChangeHandler={handleChange}
                        helperMessage={endDateHelper}
                        label="End Date"
                        type="date"
                    ></InputField>
                </FormControl>
            </Grid>
            <Grid item xs={12} sm={12} md={12} lg={6}>
                <FormControl
                    sx={{
                        marginTop: 3,
                        marginBottom: 1,
                        marginLeft: 3,
                        width: "90%",
                        maxWidth: "500px",
                    }}
                >
                    <TextField
                        title={"Reason For Leaving"}
                        id="reason"
                        value={section.reason}
                        error={reasonError}
                        onChangeHandler={handleChange}
                        helperMessage={reasonHelper}
                        label="Reason For Leaving"
                        type="text"
                    ></TextField>
                </FormControl>
            </Grid>
        </Grid>
        </div>
    );
}

export default EmploymentHistory;