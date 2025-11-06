import { Link } from "react-router-dom";
import "../LandingStyle.css";
import { NavBar } from "./NavBar";
import "../NavBar.css";

const handleFile = () => {
  console.log("file placeholder");
};

const handleUpload = () => {
  console.log("handle upload to api placeholder");
};

export const LandingPage = () => {
  return (
    <div className="landing-body">
      <div>
        <NavBar />
      </div>
      <div className="page-content">
        <div className="description">
          <h2>Description</h2>
          <p>
            This application will read your log files in a multithreaded fasion
            and allow you to parse them based on the parameters you enter below.
            It will then write to a file and allow you to download said file
            with only the lines containing what you wrote in the parameters.
          </p>
          <p>
            Note: These properties must contain a specific header you are
            searching for. Example: You are looking for log-level specific,
            choices are ERROR, WARN, INFO, etc.
          </p>
        </div>
        <div className="parameters">
          <div className="box1">
            <p>Log-Level</p>
            <input type="text" placeholder="Property 1..." />
          </div>
          <div className="box2">
            <p>Message</p>
            <input type="text" placeholder="Property 2..." />
          </div>
          <div className="box3">
            <p>Date</p>
            <input type="text" placeholder="Property 3..." />
          </div>
        </div>
        <div>
          <p>Please Submit your CSV File below</p>
        </div>
        <input type="file" onChange={handleFile}></input>
        <button className="btn" onClick={handleUpload}>
          Upload
        </button>
      </div>
    </div>
  );
};
