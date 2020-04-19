resource "aws_ecs_cluster" "main" {
  name = "cloud-kick-cluster"
}

resource "aws_lb" "date-time" {
  name               = "date-time-tf"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.elb.id]
  subnets            = aws_subnet.public.*.id
  depends_on         = [aws_security_group.elb]
}

resource "aws_alb_target_group" "date-time" {
  name        = "date-time-group"
  port        = 80
  protocol    = "HTTP"
  vpc_id      = aws_vpc.cloud_kick.id
  target_type = "ip"

  health_check {
    healthy_threshold   = "3"
    interval            = "30"
    protocol            = "HTTP"
    matcher             = "200"
    timeout             = "3"
    path                = "/actuator/health"
    unhealthy_threshold = "2"
  }
}

resource "aws_alb_listener" "front_end" {
  load_balancer_arn = aws_lb.date-time.id
  port              = var.public_port
  protocol          = "HTTP"

  default_action {
    target_group_arn = aws_alb_target_group.date-time.id
    type             = "forward"
  }
}

resource "aws_ecs_service" "date-time" {
  name            = "date-time"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.date-time.arn
  launch_type     = "FARGATE"
  desired_count   = 1

  network_configuration {
    security_groups  = [aws_security_group.task.id]
    subnets          = aws_subnet.private.*.id
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_alb_target_group.date-time.arn
    container_name   = "date-time"
    container_port   = var.container_port
  }
  depends_on = [aws_lb.date-time, aws_iam_role_policy_attachment.ecs-task-execution-role]
}

resource "aws_ecs_task_definition" "date-time" {
  family                = "date-time"
  container_definitions = file("task-definitions/date-time.json")
  network_mode             = "awsvpc"
  execution_role_arn       = aws_iam_role.task-execution-role.arn
  requires_compatibilities = ["FARGATE"]
  cpu                      = 1024
  memory                   = 2048
  depends_on = [aws_iam_role.task-execution-role]
}


resource "aws_ecr_repository" "date-time" {
  name                 = "date-time"
  image_tag_mutability = "IMMUTABLE"

  image_scanning_configuration {
    scan_on_push = false
  }
}


# IAM Stuff
resource "aws_iam_role" "task-execution-role" {
  name = "task-execution-role"
  assume_role_policy = data.aws_iam_policy_document.ecs-task-assume-role.json
}

data "aws_iam_policy_document" "ecs-task-assume-role" {
  statement {
    sid = ""
    effect = "Allow"
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

data "aws_iam_policy" "ecs-task-execution-role" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Attach policy
resource "aws_iam_role_policy_attachment" "ecs-task-execution-role" {
  role = aws_iam_role.task-execution-role.name
  policy_arn = data.aws_iam_policy.ecs-task-execution-role.arn
}