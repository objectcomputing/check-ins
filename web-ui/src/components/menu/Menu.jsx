import { Link } from "react-router-dom";
import React, { useState } from "react";
import AppBar from "@material-ui/core/AppBar";
import CssBaseline from "@material-ui/core/CssBaseline";
import Drawer from "@material-ui/core/Drawer";
import Hidden from "@material-ui/core/Hidden";
import IconButton from "@material-ui/core/IconButton";
import MenuIcon from "@material-ui/icons/Menu";
import Toolbar from "@material-ui/core/Toolbar";
import Button from "@material-ui/core/Button";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import AvatarComponent from "../avatar/Avatar";
import Modal from "react-modal";
import "./Menu.css";

const drawerWidth = 150;

const useStyles = makeStyles((theme) => ({
  root: {
    display: "flex",
  },
  drawer: {
    [theme.breakpoints.up("sm")]: {
      width: drawerWidth,
      flexShrink: 0,
    },
  },
  appBar: {
    [theme.breakpoints.up("sm")]: {
      width: `calc(100% - ${drawerWidth}px)`,
      marginLeft: drawerWidth,
    },
  },
  menuButton: {
    marginRight: theme.spacing(2),
    [theme.breakpoints.up("sm")]: {
      display: "none",
    },
  },
  // necessary for content to be below app bar
  // toolbar: theme.mixins.toolbar,
  drawerPaper: {
    width: drawerWidth,
  },
  content: {
    flexGrow: 1,
    padding: theme.spacing(3),
  },
}));

const customStyles = {
  content: {
    backgroundColor: "#3f51b5",
    display: "flex",
    justifyContent: "center",
    top: "50%",
    left: "50%",
    right: "auto",
    bottom: "auto",
    transform: "translate(-50%, -50%)",
  },
};

function Menu(props) {
  const { window } = props;
  const classes = useStyles();
  const theme = useTheme();
  const [mobileOpen, setMobileOpen] = useState(false);
  const [open, setIsOpen] = useState(false);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const linkStyle = { textDecoration: "none" };

  const drawer = (
    <div>
      <div className={classes.toolbar} />
      <h3 className="checkin">Check in!</h3>
      <Button>
        <Link style={linkStyle} to="/">
          Home
        </Link>
      </Button>
      <br />
      <Button>
        <Link style={linkStyle} to="/team">
          Team
        </Link>
      </Button>
      <br />
      <Button>
        <Link style={linkStyle} to="/resources">
          Resources
        </Link>
      </Button>
      <br />
      <Button>
        <Link style={linkStyle} to="/upload">
          Uploads
        </Link>
      </Button>
    </div>
  );

  const container =
    window !== undefined ? () => window().document.body : undefined;

  return (
    <div className={classes.root} style={{ paddingRight: `${drawerWidth}px` }}>
      <CssBaseline />
      <AppBar position="fixed" className={classes.appBar}>
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            className={classes.menuButton}
          >
            <MenuIcon />
          </IconButton>
        </Toolbar>
        <Modal
          isOpen={open}
          contentLabel="Testing"
          style={customStyles}
          ariaHideApp={false}
        >
          <div style={{ display: "flex", flexDirection: "column" }}>
            <Button onClick={() => setIsOpen(!open)} style={{ color: "white" }}>
              Login
            </Button>
            <Button onClick={() => setIsOpen(!open)} style={{ color: "white" }}>
              <Link
                style={{ color: "white", textDecoration: "none" }}
                to="/profile"
              >
                Profile
              </Link>
            </Button>
          </div>
          <div>
            <Button
              style={{
                color: "white",
                display: "flex",
                justifyContent: "flex-end",
                paddingRight: 0,
              }}
              onClick={() => {
                setIsOpen(false);
              }}
            >
              X
            </Button>
          </div>
        </Modal>
        <Button
          onClick={() => setIsOpen(!open)}
          style={{
            position: "absolute",
            right: "5px",
          }}
        >
          <AvatarComponent />
        </Button>
      </AppBar>
      <nav className={classes.drawer} aria-label="mailbox folders">
        <Hidden smUp implementation="css">
          <Drawer
            container={container}
            variant="temporary"
            anchor={theme.direction === "rtl" ? "right" : "left"}
            open={mobileOpen}
            onClose={handleDrawerToggle}
            classes={{
              paper: classes.drawerPaper,
            }}
            ModalProps={{
              keepMounted: true, // Better open performance on mobile.
            }}
          >
            {drawer}
          </Drawer>
        </Hidden>
        <Hidden xsDown implementation="css">
          <Drawer
            classes={{
              paper: classes.drawerPaper,
            }}
            variant="permanent"
            open
          >
            {drawer}
          </Drawer>
        </Hidden>
      </nav>
      <main className={classes.content}>
        <div className={classes.toolbar} />
      </main>
    </div>
  );
}

export default Menu;
