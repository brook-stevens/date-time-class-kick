variable "aws_region" {
  description = "The AWS region things are created in"
  default     = "us-east-2"
}

variable "az_count" {
  description = "Number of AZs to cover in a given region"
  default     = "2"
}

variable "public_port" {
  description = "port we ultimately expose to the user"
  default = "80"
}

variable "container_port" {
  description = "port that the app runs in within the container"
  default = "8080"
}