import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import * as yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import { NavBar } from "./NavBar";
import "../AccountPage.css";
import { useState } from "react";

export const AccountPage = ({ setIsLoggedIn }) => {
  const navigate = useNavigate();

  const handleAccountDeletion = async () => {
    const userPassword = window.prompt("Please enter your password");
    if (userPassword !== null) {
      const formData = new FormData();
      formData.append("password", userPassword);
      try {
        const response = await fetch("http://localhost:8080/account/delete", {
          method: "POST",
          credentials: "include",
          body: formData,
        });

        if (!response.ok) throw new Error("Error deleting account: ", response);
        console.log("User successfully deleted");
        navigate("/");
      } catch (error) {
        console.log(error);
      }
    }
  };

  const schema = yup.object().shape({
    oldPassword: yup.string().required("Old password is required"),
    newPassword: yup.string().min(6).max(16).required(),
    confirmPassword: yup
      .string()
      .oneOf([yup.ref("newPassword")], "Passwords do not match")
      .required(),
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(schema),
  });

  const onSubmit = async (data) => {
    console.log("Form data:", data);

    const { oldPassword, newPassword } = data;
    const formData = new FormData();
    formData.append("newPassword", newPassword);
    formData.append("oldPassword", oldPassword);

    try {
      const response = await fetch(
        "http://localhost:8080/account/change-password",
        {
          method: "POST",
          credentials: "include",
          body: formData,
        }
      );

      if (!response.ok) throw new Error("Error changing password");

      console.log("Password change successful");
      window.location.reload();
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div className="account-container">
      <NavBar />
      <div className="page-content">
        <div className="page-info">
          <h2>Welcome To The Account Page!</h2>
          <p>
            Here you can reset your password as well as view history of csv
            files uploaded
          </p>
        </div>

        <div className="account-reset">
          <p>Reset your password</p>

          <form onSubmit={handleSubmit(onSubmit)}>
            <div>
              <input
                type="password"
                placeholder="Old Password"
                {...register("oldPassword")}
              />
              <p>{errors.oldPassword?.message}</p>
            </div>

            <div>
              <input
                type="password"
                placeholder="New Password..."
                {...register("newPassword")}
              />
              <p>{errors.newPassword?.message}</p>
            </div>

            <div>
              <input
                type="password"
                placeholder="Confirm Password..."
                {...register("confirmPassword")}
              />
              <p>{errors.confirmPassword?.message}</p>
            </div>

            <input className="btn" type="submit" />
          </form>

          <div>
            <p className="account-delete">
              Delete Account? Note: This is not reversible
            </p>
            <button className="delete-btn" onClick={handleAccountDeletion}>
              Delete
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
