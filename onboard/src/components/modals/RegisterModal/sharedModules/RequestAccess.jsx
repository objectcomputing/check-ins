import React from 'react';
import Button from '@mui/material/Button';
import Divider from '@mui/material/Divider';
import Typography from '@mui/material/Typography';
import styled from 'styled-components';

const RegisterButton = styled(Button)`
  background-color: transparent;
  color: #36b769;
  margin: 0 auto;
  position: relative;
  display: block;
  width: 100%;
  max-width: 300px;
  height: 35px;

  :hover {
    background-color: #36b769;
    color: #fff;
  }
`;

function RequestAccess() {

  const handleRequestClick = () => {
    console.log('Request account, TODO');
  };

  return (
    <>
      <Divider variant="fullWidth" dark="true" className="width100Percent" />
      <Typography
        sx={{ marginTop: 1, marginBottom: 2, textAlign: 'center' }}
        children={'No pre-authorized access?'}
        variant={'h6'}
      />
      <RegisterButton onClick={handleRequestClick} variant="text">
        Request Access
      </RegisterButton>
    </>
  );
}

export default RequestAccess;
