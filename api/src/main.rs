use axum::http::StatusCode;
use axum::response::IntoResponse;
use axum::routing::method_routing;
use axum::{Json, Router};
use serde::{Deserialize, Serialize};
use std::fs::File;
use std::net::SocketAddr;
use tokio::net::TcpListener;
use uuid::Uuid;

#[tokio::main]
async fn main() {
    let addr = SocketAddr::from(([0, 0, 0, 0], 3000));
    let tcp_listener = TcpListener::bind(addr).await.unwrap();
    let router = Router::new().route("/android/backup", method_routing::post(endpoint));
    axum::serve(tcp_listener, router).await.unwrap();
}

#[derive(Debug, Deserialize, Serialize)]
struct Request {
    projects: Vec<Project>,
}

#[derive(Debug, Deserialize, Serialize)]
struct Project {
    id: usize,
    name: String,
    time_intervals: Vec<TimeInterval>,
}

#[derive(Debug, Deserialize, Serialize)]
struct TimeInterval {
    id: usize,
    start_in_milliseconds: usize,
    stop_in_milliseconds: usize,
    is_registered: bool,
}

async fn endpoint(Json(request): Json<Request>) -> impl IntoResponse {
    let id = Uuid::new_v4();
    let filename = format!("{}.json", id);
    let mut file = File::create(filename).unwrap();
    serde_json::to_writer(&mut file, &request).unwrap();
    StatusCode::NO_CONTENT
}
