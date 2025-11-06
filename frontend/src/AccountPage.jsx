import { Navigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import * as yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import { userSchema } from "./UserInfoSchema";
import { NavBar } from "./NavBar";

export const AccountPage = ({ setIsLoggedIn }) => {
  const {
    register,
    handleSubmit,
    formState: { errors }, // get the messages for the errors
  } = useForm({
    resolver: yupResolver(schema), // resolver from yup to pass the schema in
  });

  return (
    <div className="account-container">
      <NavBar />
      <div className="account-info">
        <div>
          // change username, password, email section
          <input type="text" placeholder="Username..."></input>
        </div>
        <div>// account deletion section</div>
      </div>
    </div>
  );
};
