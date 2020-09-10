import React, {useContext, useState} from "react";
import CheckinsHistory from "../components/checkin/CheckinHistory";
import CheckinDocs from "../components/checkin/CheckinDocs";
import Personnel from "../components/personnel/Personnel";
import Modal from "../components/modal/Modal";
import Container from "@material-ui/core/Container";
import Button from "@material-ui/core/Button";
import Grid from "@material-ui/core/Grid";
import GuidesPanel from "../components/guides/GuidesPanel";
import Note from "../components/notes/Note";
import {AppContext} from "../context/AppContext";
import "./CheckinsPage.css";

const CheckinsPage = () => {
    const [show, setShow] = useState(false);
    const {state} = useContext(AppContext);
    const {checkins, userProfile} = state;
    const [index, setIndex] = useState(0);
    const checkin = checkins[index];

    const showModal = () => {
        setShow(!show);
    };

    return (
        <div>
            <Container maxWidth="xl">
                <Grid
                    container
                    spacing={3}
                >
                    <Grid item sm={9} justify="center">
                        <Container maxWidth="md">
                            <div className="contents">
                                <CheckinsHistory setIndex={setIndex}/>
                            </div>
                            <CheckinDocs/>
                            <div className="modal-container">
                                <Modal close={showModal} show={show}>
                                    The checkin will no longer be able to be edited. Are you sure that you
                                    are ready to close this check-in?
                                </Modal>
                                <Button
                                    style={{
                                        backgroundColor: "#3f51b5",
                                        color: "white",
                                        display: show ? "none" : "",
                                    }}
                                    onClick={() => showModal()}
                                >
                                    Submit
                                </Button>
                            </div>
                        </Container>
                    </Grid>
                    <Grid item sm={3} justify="flex-end">
                        <Container maxWidth="md">
                            <div className="right-sidebar">
                                <Personnel/>
                                <GuidesPanel/>
                            </div>
                            {checkin && checkin.id && (
                                <Note
                                    checkin={checkin}
                                    memberName={userProfile.name}
                                />
                            )}
                        </Container>
                    </Grid>
                </Grid>
            </Container>
        </div>
    );
};

export default CheckinsPage;
