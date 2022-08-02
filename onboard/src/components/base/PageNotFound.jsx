import React from 'react';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import { Container } from '@mui/material';

// This is where you go if you've attempted to find a page that doesn't exist.
function PageNotFound() {
  return (
    <Container>
      <Grid sx={{ marginTop: 5 }} container>
        <Grid container item xs={12}>
          <Box sx={{ width: '100%' }}>
            <Typography variant='h5' align="center" sx={{m: 2}}>Sorry, that page was not found.</Typography>
          </Box>
        </Grid>
      </Grid>
    </Container>
  );
}
export default PageNotFound;
