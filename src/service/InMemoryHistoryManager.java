package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    final HashMap<Integer, Node<Task>> mapHistoryTask;
    private Node<Task> head;
    private Node<Task> tail;

    public InMemoryHistoryManager() {
        this.mapHistoryTask = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        linkLastObject(task);
    }

    @Override
    public void remove(int id) {
        removeHistoryById(id);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) getAllTasksFromHistory();
    }

    private void linkLastObject(Task task) {
        if (mapHistoryTask.containsKey(task.getId())) {
            remove(task.getId());
        }
        Node<Task> newNode = new Node<>(tail, null, task);
        if (tail != null) {
            tail.setNext(newNode);
        }
        tail = newNode;
        if (head == null) {
            head = tail;
        }
        mapHistoryTask.put(task.getId(), newNode);
    }

    private void removeHistoryById(int id) {
        Node<Task> nodeToRemove = mapHistoryTask.remove(id);
        if (nodeToRemove != null) {
            removeNode(nodeToRemove);
        }
    }

    private void removeNode(Node<Task> node) {
        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        } else {
            head = node.getNext();
        }
        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        } else {
            tail = node.getPrev();
        }
        if (node.getPrev() == null && node.getNext() == null) {
            head = null;
            tail = null;
        }
    }

    private List<Task> getAllTasksFromHistory() {
        List<model.Task> tasks = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            tasks.add(current.getElement());
            current = current.getNext();
        }
        return tasks;
    }

    private static class Node<T extends Task> {
        Node<T> prev;
        Node<T> next;
        T element;

        public Node(Node<T> prev, Node<T> next, T element) {
            this.prev = prev;
            this.next = next;
            this.element = element;
        }

        public Node<T> getPrev() {
            return prev;
        }

        public void setPrev(Node<T> prev) {
            this.prev = prev;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

        public T getElement() {
            return element;
        }
    }
}