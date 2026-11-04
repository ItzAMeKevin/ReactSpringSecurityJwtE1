import { describe, it, expect } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter, Route, Routes, useLocation, useNavigate } from "react-router-dom";
import useHighlightWhenNotified from "./useHighlightWhenNotified.js";

const Page = () => {
    const { isHighlighted, clearHighlight } = useHighlightWhenNotified();
    const location = useLocation();
    const navigate = useNavigate();
    return (
        <>
            <p data-testid="cv-1">{isHighlighted("cv", 1) ? "highlighted" : "normal"}</p>
            <p data-testid="offer-1">{isHighlighted("offer", 1) ? "highlighted" : "normal"}</p>
            <p data-testid="state">{JSON.stringify(location.state)}</p>
            <button onClick={clearHighlight}>clear</button>
            <button onClick={() => navigate("/page", { state: { highlight: { kind: "offer", id: 1 } } })}>
                go to offer
            </button>
        </>
    );
};

const renderPage = (state) =>
    render(
        <MemoryRouter initialEntries={[{ pathname: "/page", state }]}>
            <Routes>
                <Route path="/page" element={<Page />} />
            </Routes>
        </MemoryRouter>
    );

describe("useHighlightFromNavigation", () => {
    it("should highlight the item from the navigation state", () => {
        renderPage({ highlight: { kind: "cv", id: 1 } });

        expect(screen.getByTestId("cv-1")).toHaveTextContent("highlighted");
        expect(screen.getByTestId("offer-1")).toHaveTextContent("normal");
    });

    it("should highlight nothing without navigation state", () => {
        renderPage(null);

        expect(screen.getByTestId("cv-1")).toHaveTextContent("normal");
        expect(screen.getByTestId("offer-1")).toHaveTextContent("normal");
    });

    it("should clear the navigation state but keep the highlight", () => {
        renderPage({ highlight: { kind: "cv", id: 1 } });

        expect(screen.getByTestId("state")).toHaveTextContent("null");
        expect(screen.getByTestId("cv-1")).toHaveTextContent("highlighted");
    });

    it("should remove the highlight when clearHighlight is called", () => {
        renderPage({ highlight: { kind: "cv", id: 1 } });

        fireEvent.click(screen.getByText("clear"));

        expect(screen.getByTestId("cv-1")).toHaveTextContent("normal");
    });

    it("should move the highlight when a new one arrives", () => {
        renderPage({ highlight: { kind: "cv", id: 1 } });

        fireEvent.click(screen.getByText("go to offer"));

        expect(screen.getByTestId("cv-1")).toHaveTextContent("normal");
        expect(screen.getByTestId("offer-1")).toHaveTextContent("highlighted");
    });
});
