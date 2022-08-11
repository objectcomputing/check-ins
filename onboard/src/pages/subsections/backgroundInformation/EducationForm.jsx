import React, { useEffect, useState } from "react";
import FormControl from "@mui/material/FormControl";
import FormLabel from "@mui/material/FormLabel";
import Grid from "@mui/material/Grid";
import InputField from "../../../components/inputs/InputField";
import TextField from "../../../components/inputs/TextField";

function EducationForm({ section, educationSections, setEducationSections }) {
    const [filteredState, setFilteredState] = useState([]);

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

    useEffect(() => {
        let newFilteredState = educationSections.filter(currentObjs => currentObjs.id !== section.id);
        setFilteredState([...newFilteredState]);
        console.log("Filtered for section ", section.id)
        console.log(newFilteredState)
        console.log("This is the education section for section ", section.id)
        console.log(educationSections)
    }, [educationSections])

    function handleChange(event) {
        const e = event;
        const val = e.target.value;
        const name = e.target.name;

        setEducationSections([
            ...filteredState, {
                ...section,
                [name]: val
            }
        ]);

        if (name === "highestDegree") {

            if (val.length > 0) {
                setHighestDegreeError(false);
                setHighestDegreeHelper("");
            } else {
                setHighestDegreeError(true);
                setHighestDegreeHelper("Please enter in your highest degree earned");
            }
        }
        // } else if (name === "institution") {
        //     const newState = [...filteredState, {
        //         ...section,
        //         institution: val
        //     }];
        //     setEducationSections(newState);
        //     if (val.length > 0) {
        //         setInsitutionError(false);
        //         setInsitutionHelper("");
        //     } else {
        //         setInsitutionError(true);
        //         setInsitutionHelper(
        //             "Please enter in the name of the institution you studied at"
        //         );
        //     }
        // } else if (name === "locate") {
        //     const newState = [...filteredState, {
        //         ...section,
        //         location: val
        //     }];
        //     setEducationSections(newState);
        //     if (val.length > 0) {
        //         setLocationError(false);
        //         setLocationHelper("");
        //     } else {
        //         setLocationError(true);
        //         setLocationHelper("Please enter the location of the institution");
        //     }
        // } else if (name === "degree") {
        //     const newState = [...filteredState, {
        //         ...section,
        //         degree: val
        //     }];
        //     setEducationSections(newState);
        //     if (val.length > 0) {
        //         setDegreeError(false);
        //         setDegreeHelper("");
        //     } else {
        //         setDegreeError(true);
        //         setDegreeHelper("Please enter your degree");
        //     }
        // } else if (name === "major") {
        //     const newState = [...filteredState, {
        //         ...section,
        //         major: val
        //     }];
        //     setEducationSections(newState);
        //     if (val.length > 0) {
        //         setMajorError(false);
        //         setMajorHelper("");
        //     } else {
        //         setMajorError(true);
        //         setMajorHelper("Please enter in your chosen major");
        //     }
        // } else if (name === "completionDate") {
        //     const newState = [...filteredState, {
        //         ...section,
        //         completionDate: val
        //     }];
        //     setEducationSections(newState);
        //     if (val.length > 0) {
        //         setCompletionDateError(false);
        //         setCompletionDateHelper("");
        //     } else {
        //         setCompletionDateError(true);
        //         setCompletionDateHelper("Please enter in a date");
        //     }
        // } else if (name === "additionalInformation") {
        //     const newState = [...filteredState, {
        //         ...section,
        //         additionalInformation: val
        //     }];
        //     setEducationSections(newState);
        // }
    }

    return (
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
                        title={"Highest Degree Level Accuired:"}
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
                        id="locate"
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
    );
}

export default EducationForm;
