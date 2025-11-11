import { useForm } from "react-hook-form";
import * as yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import "../LoginStyle.css";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";

export const LoginForm = () => {
  const navigate = useNavigate("");
  // schema for object being created from the text boxes
  const schema = yup.object().shape({
    username: yup.string().required("Your username is required!"),
    password: yup
      .string()
      .min(6)
      .max(16)
      .required("Your password is required!"),
  });

  const {
    register,
    handleSubmit,
    formState: { errors }, // get the messages for the errors
  } = useForm({
    resolver: yupResolver(schema), // resolver from yup to pass the schema in
  });

  /*
    Need to handle this onSubmit we need to take data to backend and
    forward the user to the landing page
  */
  const onSubmit = async (data) => {
    try {
      // await pauses execution of function until promise resolves (fetch returns a promise)
      // a promise gives us a result later (the response)
      const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        throw new Error("Login failure");
      }

      const result = await response.text();
      console.log("Login successful", result);
      navigate("/landing");
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div className="login-body">
      <div className="login-container">
        <div>
          <h2>Login To Your Account</h2>
        </div>
        <div>
          <form onSubmit={handleSubmit(onSubmit)}>
            <input
              type="text"
              placeholder="Username..."
              {...register("username")}
            ></input>
            <p className="error-msg">{errors.username?.message}</p>

            <input
              type="text"
              placeholder="Password..."
              {...register("password")}
            ></input>
            <p className="error-msg">{errors.password?.message}</p>

            <p>
              No Account?{" "}
              <Link className="link" to="/create-account">
                Register Today!
              </Link>
            </p>
            <input className="btn" type="submit"></input>
          </form>
        </div>
      </div>
    </div>
  );
};
