import React, { useState } from "react";
import FormControl from "@mui/material/FormControl";
import FormLabel from "@mui/material/FormLabel";
import Grid from "@mui/material/Grid";
import InputField from "../../../components/inputs/InputField";
import TextField from "../../../components/inputs/TextField";
import { Divider } from "@mui/material";

function EducationForm({ section, educationSections, setEducationSections }) {

    const [highestDegreeHelper, setHighestDegreeHelper] = useState("");
    const [institutionHelper, setInsitutionHelper] = useState("");
    const [locationHelper, setLocationHelper] = useState("");
    const [degreeHelper, setDegreeHelper] = useState("");
    const [majorHelper, setMajorHelper] = useState("");
    const [completionDateHelper, setCompletionDateHelper] = useState("");

    const [highestDegreeError, setHighestDegreeError] = useState(false);
    const [institutionError, setInsitutionError] = useState(false);
    const [locationError, setLocationError] = useState(false);
    const [degreeError, setDegreeError] = useState(false);
    const [majorError, setMajorError] = useState(false);
    const [completionDateError, setCompletionDateError] = useState(false);

    function adjustState(name, val) {
        let newArr = [];
        educationSections.forEach((eSection) => {
            if (eSection.id === section.id) {
                newArr.push({ ...section, [name]: val });
            } else {
                newArr.push(eSection);
            }
        })
        return newArr;
    }

    function handleChange(event) {
        const e = event;
        const val = e.target.value;
        const name = e.target.name;

        let newState = adjustState(name, val);
        setEducationSections(newState);

        if (name === "highestDegree") {
            if (val.length > 0) {
                setHighestDegreeError(false);
                setHighestDegreeHelper("");
            } else {
                setHighestDegreeError(true);
                setHighestDegreeHelper("Please enter in your highest degree earned.");
            }
        } else if (name === "institution") {
            if (val.length > 0) {
                setInsitutionError(false);
                setInsitutionHelper("");
            } else {
                setInsitutionError(true);
                setInsitutionHelper(
                    "Please enter in the name of the institution you studied at."
                );
            }
        } else if (name === "location") {
            if (val.length > 0) {
                setLocationError(false);
                setLocationHelper("");
            } else {
                setLocationError(true);
                setLocationHelper("Please enter the location of the institution.");
            }
        } else if (name === "degree") {
            if (val.length > 0) {
                setDegreeError(false);
                setDegreeHelper("");
            } else {
                setDegreeError(true);
                setDegreeHelper("Please enter your degree.");
            }
        } else if (name === "major") {
            if (val.length > 0) {
                setMajorError(false);
                setMajorHelper("");
            } else {
                setMajorError(true);
                setMajorHelper("Please enter in your chosen major.");
            }
        } else if (name === "completionDate") {
            if (val.length > 0) {
                setCompletionDateError(false);
                setCompletionDateHelper("");
            } else {
                setCompletionDateError(true);
                setCompletionDateHelper("Please enter in a date.");
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
                <Grid item xs={12} sm={12} md={12} lg={6} >
                    <FormControl
                        sx={{
                            marginTop: "8px",
                            marginBottom: 1,
                            marginLeft: 3,
                            width: "90%",
                            maxWidth: "500px",
                        }}
                    >
                        <InputField
                            title={"Highest Degree Level Acquired:"}
                            id="highestDegree"
                            value={section.highestDegree}
                            autoFocus={true}
                            error={highestDegreeError}
                            onChangeHandler={handleChange}
                            helperMessage={highestDegreeHelper}
                            label="Highest Degree Level"
                            type="text"
                        ></InputField>
                    </FormControl>
                </Grid>
                <Grid item xs={12} sm={12} md={12} lg={6}>
                    <FormControl
                        sx={{
                            my: 1,
                            marginLeft: 3,
                            width: "90%",
                            maxWidth: "500px",
                        }}
                    >
                        <InputField
                            title="Institution:"
                            id="institution"
                            label="Institution"
                            value={section.institution}
                            error={institutionError}
                            helperMessage={institutionHelper}
                            onChangeHandler={handleChange}
                            type={"text"}
                        ></InputField>
                    </FormControl>
                </Grid>

                <Grid item xs={12} sm={12} md={12} lg={6}>
                    <FormControl
                        sx={{
                            my: 1,
                            marginLeft: 3,
                            width: "90%",
                            maxWidth: "500px",
                        }}
                    >
                        <InputField
                            title="Location:"
                            id="location"
                            label="Location"
                            value={section.location}
                            error={locationError}
                            helperMessage={locationHelper}
                            onChangeHandler={handleChange}
                            type={"text"}
                        ></InputField>
                    </FormControl>
                </Grid>

                <Grid item xs={12} sm={12} md={12} lg={6}>
                    <FormControl
                        sx={{
                            my: 1,
                            marginLeft: 3,
                            width: "90%",
                            maxWidth: "500px",
                        }}
                    >
                        <InputField
                            title="Degree:"
                            id="degree"
                            label="Degree"
                            value={section.degree}
                            error={degreeError}
                            helperMessage={degreeHelper}
                            onChangeHandler={handleChange}
                            type={"text"}
                        ></InputField>
                    </FormControl>
                </Grid>

                <Grid item xs={12} sm={12} md={12} lg={6}>
                    <FormControl
                        sx={{
                            my: 1,
                            marginLeft: 3,
                            width: "90%",
                            maxWidth: "500px",
                        }}
                    >
                        <InputField
                            title="Major / Concentration:"
                            id="major"
                            label="Major"
                            value={section.major}
                            error={majorError}
                            helperMessage={majorHelper}
                            onChangeHandler={handleChange}
                            type={"text"}
                        ></InputField>
                    </FormControl>
                </Grid>

                <Grid item xs={12} sm={12} md={12} lg={6}>
                    <FormControl
                        sx={{
                            my: 1,
                            marginLeft: 3,
                            width: "90%",
                            maxWidth: "500px",
                        }}
                    >
                        <FormLabel>Completion Date:</FormLabel>
                        <InputField
                            id="completionDate"
                            value={section.completionDate}
                            error={completionDateError}
                            helperMessage={completionDateHelper}
                            onChangeHandler={handleChange}
                            type="date"
                        />
                    </FormControl>
                </Grid>

                <Grid item xs={12} sm={12} md={12} lg={6}>
                    <FormControl
                        sx={{
                            my: 1,
                            marginLeft: 3,
                            width: "90%",
                            maxWidth: "500px",
                        }}
                    >
                        <TextField
                            title="Additional Research, Coursework, and Certification:"
                            id="additionalInformation"
                            value={section.additionalInformation}
                            onChangeHandler={handleChange}
                            type={"text"}
                        ></TextField>
                    </FormControl>
                </Grid>
            </Grid>
        </div>
    );
}

export default EducationForm;
