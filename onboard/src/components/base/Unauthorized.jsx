import React from 'react';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import { Container } from '@mui/material';
import { appearingText } from 'utils/helperFunctions';

// This is where you go if you've attempted to find a page you're not authorized to view.
function Unauthorized() {
  return (
    <Container>
      <Grid sx={{ marginTop: 5 }} container>
        <Grid container item xs={12}>
          <Box sx={{ width: '100%' }}>
            <Typography variant="h5" align="center" sx={{ m: 2 }}>
              {appearingText(
                'Sorry, you are not authorized to view this. Please ask your admin to add access for you.'
              )}
            </Typography>
          </Box>
        </Grid>
      </Grid>
    </Container>
  );
}
export default Unauthorized;
