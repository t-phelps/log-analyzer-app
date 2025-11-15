import { Link } from "react-router-dom";
import "../LandingStyle.css";
import { NavBar } from "./NavBar";
import "../NavBar.css";
import { useState } from "react";

export const LandingPage = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [inputValue, setInputValue] = useState("");

  const handleInput = (event) => {
    const input = event.target.value.toLowerCase();
    // could put this in a set and if it adds to set then its invalid
    if (
      input !== "error" ||
      input !== "warn" ||
      input !== "info" ||
      input !== "debug" ||
      input !== "fatal" ||
      input !== "trace"
    ) {
      return alert("Not a valid Log Level");
    }
    setInputValue(event.target.value);
  };

  const handleFile = (event) => {
    console.log("Files selected:", event.target.files);
    setSelectedFile(event.target.files[0]);
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      return alert("Please select a file first");
    } else {
      const formData = new FormData();
      formData.append("file", selectedFile);
      formData.append("input", inputValue);

      try {
        const response = await fetch("http://localhost:8080/upload/file", {
          method: "POST",
          body: formData,
          credentials: "include",
        });

        if (!response.ok) {
          throw new Error("Upload Failed: ", response);
        }

        const blob = await response.blob();

        //temp url for blob
        const url = window.URL.createObjectURL(blob);

        const a = document.createElement("a");
        a.href = url;

        const disposition = response.headers.get("Content-Disposition");
        let fileName = "downloaded_file";
        if (disposition && disposition.includes("filename=")) {
          fileName = disposition
            .split("filename=")[1]
            .replace(/['"]/g, "")
            .trim();
        }
        a.download = fileName;

        document.body.appendChild(a);
        a.click();

        // Clean up
        a.remove();
        window.URL.revokeObjectURL(url);
      } catch (err) {
        console.log("Error uploading file to back-end");
      }
    }
  };

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
            <input
              type="text"
              placeholder="Property 1..."
              onChange={handleInput}
            />
          </div>
        </div>
        <div>
          <p>Please Submit your CSV File below</p>
        </div>
        <form
          className="uploadForm"
          onSubmit={(e) => {
            e.preventDefault(); // prevent page reload
            handleUpload();
          }}
        >
          <input type="file" id="fileInput" name="file" onChange={handleFile} />
          <button className="btn" type="submit">
            Upload
          </button>
        </form>
      </div>
    </div>
  );
};
