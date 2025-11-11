import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";

export const CreateAccountForm = ({ setIsLoggedIn }) => {
  const navigate = useNavigate("");
  const schema = yup.object().shape({
    email: yup
      .string()
      .email("This is not a valid email!")
      .matches(
        /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
        "Email must be a valid address with a domain"
      )
      .required("Your Email Is Requried"),
    username: yup.string().required(),
    password: yup.string().min(6).max(16).required(),
    confirmPassword: yup
      .string()
      .oneOf([yup.ref("password"), null], "Passwords Don't Match!")
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
    try {
      // await pauses execution of function until promise resolves (fetch returns a promise)
      // a promise gives us a result later (the response)
      const response = await fetch("http://localhost:8080/auth/create", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(data),
      });
      if (!response.ok) {
        throw new Error("Invalid Credentials");
      }

      const result = await response.text();
      console.log("Login Successful", result);
      setIsLoggedIn(true);
      navigate("/landing");
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div className="login-body">
      <div className="header">Welcome To Log Analyzer!</div>
      <div className="login-container">
        <h2>Register Today!</h2>
        <form onSubmit={handleSubmit(onSubmit)}>
          <input type="text" placeholder="Email" {...register("email")} />
          <p className="error-msg">{errors.email?.message}</p>

          <input type="text" placeholder="Username" {...register("username")} />
          <p className="error-msg">{errors.username?.message}</p>

          <input
            type="password"
            placeholder="Password"
            {...register("password")}
          />
          <p className="error-msg">{errors.password?.message}</p>

          <input
            type="password"
            placeholder="Confirm Password"
            {...register("confirmPassword")}
          />
          <p className="error-msg">{errors.confirmPassword?.message}</p>

          <p className="register-msg">
            Already Registered?{" "}
            <Link className="link" to="/">
              Login!
            </Link>
          </p>
          <input className="btn" type="submit" />
        </form>
      </div>
    </div>
  );
};
