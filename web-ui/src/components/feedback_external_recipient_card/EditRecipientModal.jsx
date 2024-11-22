import React from 'react';
import { Box, Typography, TextField, Button, Modal, Select, MenuItem } from '@mui/material';
import PropTypes from 'prop-types';

const modalStyle = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    bgcolor: 'background.paper',
    boxShadow: 24,
    p: 4,
};

const EditRecipientModal = ({ open, onClose, profile, onChange, onSubmit }) => {
    return (
        <Modal open={open} onClose={onClose}>
            <Box sx={{ ...modalStyle, width: 400 }}>
                <Typography variant="h6" component="h2">
                    Edit Recipient Profile
                </Typography>
                <TextField
                    label="First Name"
                    name="firstName"
                    value={profile.firstName}
                    onChange={onChange}
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Last Name"
                    name="lastName"
                    value={profile.lastName}
                    onChange={onChange}
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Email"
                    name="email"
                    value={profile.email}
                    onChange={onChange}
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Company Name"
                    name="companyName"
                    value={profile.companyName}
                    onChange={onChange}
                    fullWidth
                    margin="normal"
                />
                <Select
                    label="Status"
                    name="inactive"
                    value={profile.inactive ? 'Inactive' : 'Active'}
                    onChange={(e) => onChange({ target: { name: 'inactive', value: e.target.value === 'Inactive' } })}
                    fullWidth
                    margin="normal"
                >
                    <MenuItem value="Active">Active</MenuItem>
                    <MenuItem value="Inactive">Inactive</MenuItem>
                </Select>
                <br/>
                <Button onClick={onSubmit} variant="contained" color="primary">
                    Save
                </Button>
            </Box>
        </Modal>
    );
};

EditRecipientModal.propTypes = {
    open: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    profile: PropTypes.object.isRequired,
    onChange: PropTypes.func.isRequired,
    onSubmit: PropTypes.func.isRequired,
};

export default EditRecipientModal;
