import { Box, Card, CardContent, CardHeader, makeStyles, Typography } from '@material-ui/core'
import React from 'react'
import AvatarComponent from '../avatar/Avatar'

const useStyles = makeStyles((theme) => ({
  dimensions: {
    width: '100%',
    height: '100%',
    display: "flex",
    flexDirection: "column",
    justifyContent: "space-around"
  },
  small: {
    width: theme.spacing(6),
    height: theme.spacing(6),
    margin: theme.spacing(1),
  },
  large: {
    width: theme.spacing(8),
    height: theme.spacing(8),
    margin: theme.spacing(1),
  },
  title: {
    fontSize: 32,
  },
}));

export default function Kudos({kudosTo, kudosFrom, content}) {
  const classes = useStyles();
  return (
    <Card classes={{root: classes.dimensions}}>
      <Box display="flex" justifyContent="center">
        <CardHeader
          avatar={
            <AvatarComponent className={classes.large} imageUrl={kudosTo.imageUrl} />
          }
          // title={`Kudos to ${kudosTo.name}`}
          disableTypography
          title={
            <Typography className={classes.title}>
              {`Kudos to ${kudosTo.name} 🎉`}
            </Typography>
          }
        />
      </Box>

      <CardContent>
      <Typography>
        <Box component={'span'} textAlign="center">
            {content}
        </Box>
      </Typography>
      {kudosFrom && 
        <Box display="flex" justifyContent="flex-end" alignItems="center">
          <Typography variant="subtitle2">
              {`${kudosFrom.name} - ${kudosFrom.title}`}
            </Typography>
          <AvatarComponent className={classes.small} imageUrl={kudosFrom.imageUrl} />
        </Box>
      }
      </CardContent>
    </Card>
  )
}


